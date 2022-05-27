jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ResponsableRestaurantService } from '../service/responsable-restaurant.service';
import { IResponsableRestaurant, ResponsableRestaurant } from '../responsable-restaurant.model';

import { ResponsableRestaurantUpdateComponent } from './responsable-restaurant-update.component';

describe('Component Tests', () => {
  describe('ResponsableRestaurant Management Update Component', () => {
    let comp: ResponsableRestaurantUpdateComponent;
    let fixture: ComponentFixture<ResponsableRestaurantUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let responsableRestaurantService: ResponsableRestaurantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ResponsableRestaurantUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ResponsableRestaurantUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ResponsableRestaurantUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      responsableRestaurantService = TestBed.inject(ResponsableRestaurantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 456 };

        activatedRoute.data = of({ responsableRestaurant });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(responsableRestaurant));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ResponsableRestaurant>>();
        const responsableRestaurant = { id: 123 };
        jest.spyOn(responsableRestaurantService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ responsableRestaurant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: responsableRestaurant }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(responsableRestaurantService.update).toHaveBeenCalledWith(responsableRestaurant);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ResponsableRestaurant>>();
        const responsableRestaurant = new ResponsableRestaurant();
        jest.spyOn(responsableRestaurantService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ responsableRestaurant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: responsableRestaurant }));
        saveSubject.complete();

        // THEN
        expect(responsableRestaurantService.create).toHaveBeenCalledWith(responsableRestaurant);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ResponsableRestaurant>>();
        const responsableRestaurant = { id: 123 };
        jest.spyOn(responsableRestaurantService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ responsableRestaurant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(responsableRestaurantService.update).toHaveBeenCalledWith(responsableRestaurant);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
