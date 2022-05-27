jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { RestaurantService } from '../service/restaurant.service';
import { IRestaurant, Restaurant } from '../restaurant.model';
import { IResponsableRestaurant } from 'app/entities/responsable-restaurant/responsable-restaurant.model';
import { ResponsableRestaurantService } from 'app/entities/responsable-restaurant/service/responsable-restaurant.service';

import { RestaurantUpdateComponent } from './restaurant-update.component';

describe('Component Tests', () => {
  describe('Restaurant Management Update Component', () => {
    let comp: RestaurantUpdateComponent;
    let fixture: ComponentFixture<RestaurantUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let restaurantService: RestaurantService;
    let responsableRestaurantService: ResponsableRestaurantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RestaurantUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(RestaurantUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RestaurantUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      restaurantService = TestBed.inject(RestaurantService);
      responsableRestaurantService = TestBed.inject(ResponsableRestaurantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call ResponsableRestaurant query and add missing value', () => {
        const restaurant: IRestaurant = { id: 456 };
        const ResponsableRestaurant: IResponsableRestaurant = { id: 84140 };
        restaurant.ResponsableRestaurant = ResponsableRestaurant;

        const responsableRestaurantCollection: IResponsableRestaurant[] = [{ id: 41406 }];
        jest.spyOn(responsableRestaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: responsableRestaurantCollection })));
        const additionalResponsableRestaurants = [ResponsableRestaurant];
        const expectedCollection: IResponsableRestaurant[] = [...additionalResponsableRestaurants, ...responsableRestaurantCollection];
        jest.spyOn(responsableRestaurantService, 'addResponsableRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ restaurant });
        comp.ngOnInit();

        expect(responsableRestaurantService.query).toHaveBeenCalled();
        expect(responsableRestaurantService.addResponsableRestaurantToCollectionIfMissing).toHaveBeenCalledWith(
          responsableRestaurantCollection,
          ...additionalResponsableRestaurants
        );
        expect(comp.responsableRestaurantsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const restaurant: IRestaurant = { id: 456 };
        const ResponsableRestaurant: IResponsableRestaurant = { id: 80765 };
        restaurant.ResponsableRestaurant = ResponsableRestaurant;

        activatedRoute.data = of({ restaurant });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(restaurant));
        expect(comp.responsableRestaurantsSharedCollection).toContain(ResponsableRestaurant);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Restaurant>>();
        const restaurant = { id: 123 };
        jest.spyOn(restaurantService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ restaurant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: restaurant }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(restaurantService.update).toHaveBeenCalledWith(restaurant);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Restaurant>>();
        const restaurant = new Restaurant();
        jest.spyOn(restaurantService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ restaurant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: restaurant }));
        saveSubject.complete();

        // THEN
        expect(restaurantService.create).toHaveBeenCalledWith(restaurant);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Restaurant>>();
        const restaurant = { id: 123 };
        jest.spyOn(restaurantService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ restaurant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(restaurantService.update).toHaveBeenCalledWith(restaurant);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackResponsableRestaurantById', () => {
        it('Should return tracked ResponsableRestaurant primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackResponsableRestaurantById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
