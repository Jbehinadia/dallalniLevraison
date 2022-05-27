jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ResponsableRestaurantService } from '../service/responsable-restaurant.service';
import { IResponsableRestaurant, ResponsableRestaurant } from '../responsable-restaurant.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

import { ResponsableRestaurantUpdateComponent } from './responsable-restaurant-update.component';

describe('Component Tests', () => {
  describe('ResponsableRestaurant Management Update Component', () => {
    let comp: ResponsableRestaurantUpdateComponent;
    let fixture: ComponentFixture<ResponsableRestaurantUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let responsableRestaurantService: ResponsableRestaurantService;
    let restaurantService: RestaurantService;

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
      restaurantService = TestBed.inject(RestaurantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call restaurant query and add missing value', () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 456 };
        const restaurant: IRestaurant = { id: 44572 };
        responsableRestaurant.restaurant = restaurant;

        const restaurantCollection: IRestaurant[] = [{ id: 55030 }];
        jest.spyOn(restaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantCollection })));
        const expectedCollection: IRestaurant[] = [restaurant, ...restaurantCollection];
        jest.spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ responsableRestaurant });
        comp.ngOnInit();

        expect(restaurantService.query).toHaveBeenCalled();
        expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(restaurantCollection, restaurant);
        expect(comp.restaurantsCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const responsableRestaurant: IResponsableRestaurant = { id: 456 };
        const restaurant: IRestaurant = { id: 84959 };
        responsableRestaurant.restaurant = restaurant;

        activatedRoute.data = of({ responsableRestaurant });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(responsableRestaurant));
        expect(comp.restaurantsCollection).toContain(restaurant);
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
