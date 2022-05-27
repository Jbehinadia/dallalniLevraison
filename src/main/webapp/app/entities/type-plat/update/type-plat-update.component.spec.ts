jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TypePlatService } from '../service/type-plat.service';
import { ITypePlat, TypePlat } from '../type-plat.model';

import { TypePlatUpdateComponent } from './type-plat-update.component';

describe('Component Tests', () => {
  describe('TypePlat Management Update Component', () => {
    let comp: TypePlatUpdateComponent;
    let fixture: ComponentFixture<TypePlatUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let typePlatService: TypePlatService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TypePlatUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TypePlatUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TypePlatUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      typePlatService = TestBed.inject(TypePlatService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const typePlat: ITypePlat = { id: 456 };

        activatedRoute.data = of({ typePlat });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(typePlat));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TypePlat>>();
        const typePlat = { id: 123 };
        jest.spyOn(typePlatService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ typePlat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: typePlat }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(typePlatService.update).toHaveBeenCalledWith(typePlat);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TypePlat>>();
        const typePlat = new TypePlat();
        jest.spyOn(typePlatService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ typePlat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: typePlat }));
        saveSubject.complete();

        // THEN
        expect(typePlatService.create).toHaveBeenCalledWith(typePlat);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TypePlat>>();
        const typePlat = { id: 123 };
        jest.spyOn(typePlatService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ typePlat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(typePlatService.update).toHaveBeenCalledWith(typePlat);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
