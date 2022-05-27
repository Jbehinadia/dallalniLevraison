jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PlatService } from '../service/plat.service';
import { IPlat, Plat } from '../plat.model';
import { IMenu } from 'app/entities/menu/menu.model';
import { MenuService } from 'app/entities/menu/service/menu.service';
import { ITypePlat } from 'app/entities/type-plat/type-plat.model';
import { TypePlatService } from 'app/entities/type-plat/service/type-plat.service';

import { PlatUpdateComponent } from './plat-update.component';

describe('Component Tests', () => {
  describe('Plat Management Update Component', () => {
    let comp: PlatUpdateComponent;
    let fixture: ComponentFixture<PlatUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let platService: PlatService;
    let menuService: MenuService;
    let typePlatService: TypePlatService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PlatUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PlatUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PlatUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      platService = TestBed.inject(PlatService);
      menuService = TestBed.inject(MenuService);
      typePlatService = TestBed.inject(TypePlatService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Menu query and add missing value', () => {
        const plat: IPlat = { id: 456 };
        const menu: IMenu = { id: 19784 };
        plat.menu = menu;

        const menuCollection: IMenu[] = [{ id: 72149 }];
        jest.spyOn(menuService, 'query').mockReturnValue(of(new HttpResponse({ body: menuCollection })));
        const additionalMenus = [menu];
        const expectedCollection: IMenu[] = [...additionalMenus, ...menuCollection];
        jest.spyOn(menuService, 'addMenuToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        expect(menuService.query).toHaveBeenCalled();
        expect(menuService.addMenuToCollectionIfMissing).toHaveBeenCalledWith(menuCollection, ...additionalMenus);
        expect(comp.menusSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TypePlat query and add missing value', () => {
        const plat: IPlat = { id: 456 };
        const typePlat: ITypePlat = { id: 57950 };
        plat.typePlat = typePlat;

        const typePlatCollection: ITypePlat[] = [{ id: 98224 }];
        jest.spyOn(typePlatService, 'query').mockReturnValue(of(new HttpResponse({ body: typePlatCollection })));
        const additionalTypePlats = [typePlat];
        const expectedCollection: ITypePlat[] = [...additionalTypePlats, ...typePlatCollection];
        jest.spyOn(typePlatService, 'addTypePlatToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        expect(typePlatService.query).toHaveBeenCalled();
        expect(typePlatService.addTypePlatToCollectionIfMissing).toHaveBeenCalledWith(typePlatCollection, ...additionalTypePlats);
        expect(comp.typePlatsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const plat: IPlat = { id: 456 };
        const menu: IMenu = { id: 60240 };
        plat.menu = menu;
        const typePlat: ITypePlat = { id: 59712 };
        plat.typePlat = typePlat;

        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(plat));
        expect(comp.menusSharedCollection).toContain(menu);
        expect(comp.typePlatsSharedCollection).toContain(typePlat);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Plat>>();
        const plat = { id: 123 };
        jest.spyOn(platService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: plat }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(platService.update).toHaveBeenCalledWith(plat);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Plat>>();
        const plat = new Plat();
        jest.spyOn(platService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: plat }));
        saveSubject.complete();

        // THEN
        expect(platService.create).toHaveBeenCalledWith(plat);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Plat>>();
        const plat = { id: 123 };
        jest.spyOn(platService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ plat });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(platService.update).toHaveBeenCalledWith(plat);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackMenuById', () => {
        it('Should return tracked Menu primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackMenuById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTypePlatById', () => {
        it('Should return tracked TypePlat primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTypePlatById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
