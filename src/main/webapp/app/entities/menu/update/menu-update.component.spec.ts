jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MenuService } from '../service/menu.service';
import { IMenu, Menu } from '../menu.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

import { MenuUpdateComponent } from './menu-update.component';

describe('Component Tests', () => {
  describe('Menu Management Update Component', () => {
    let comp: MenuUpdateComponent;
    let fixture: ComponentFixture<MenuUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let menuService: MenuService;
    let restaurantService: RestaurantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [MenuUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(MenuUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MenuUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      menuService = TestBed.inject(MenuService);
      restaurantService = TestBed.inject(RestaurantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Restaurant query and add missing value', () => {
        const menu: IMenu = { id: 456 };
        const restaurant: IRestaurant = { id: 30305 };
        menu.restaurant = restaurant;

        const restaurantCollection: IRestaurant[] = [{ id: 47065 }];
        jest.spyOn(restaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantCollection })));
        const additionalRestaurants = [restaurant];
        const expectedCollection: IRestaurant[] = [...additionalRestaurants, ...restaurantCollection];
        jest.spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ menu });
        comp.ngOnInit();

        expect(restaurantService.query).toHaveBeenCalled();
        expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(restaurantCollection, ...additionalRestaurants);
        expect(comp.restaurantsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const menu: IMenu = { id: 456 };
        const restaurant: IRestaurant = { id: 44189 };
        menu.restaurant = restaurant;

        activatedRoute.data = of({ menu });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(menu));
        expect(comp.restaurantsSharedCollection).toContain(restaurant);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Menu>>();
        const menu = { id: 123 };
        jest.spyOn(menuService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ menu });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: menu }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(menuService.update).toHaveBeenCalledWith(menu);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Menu>>();
        const menu = new Menu();
        jest.spyOn(menuService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ menu });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: menu }));
        saveSubject.complete();

        // THEN
        expect(menuService.create).toHaveBeenCalledWith(menu);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Menu>>();
        const menu = { id: 123 };
        jest.spyOn(menuService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ menu });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(menuService.update).toHaveBeenCalledWith(menu);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackRestaurantById', () => {
        it('Should return tracked Restaurant primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackRestaurantById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
