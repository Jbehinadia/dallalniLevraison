import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IRestaurant, Restaurant } from '../restaurant.model';
import { RestaurantService } from '../service/restaurant.service';
import { IResponsableRestaurant } from 'app/entities/responsable-restaurant/responsable-restaurant.model';
import { ResponsableRestaurantService } from 'app/entities/responsable-restaurant/service/responsable-restaurant.service';

@Component({
  selector: 'jhi-restaurant-update',
  templateUrl: './restaurant-update.component.html',
})
export class RestaurantUpdateComponent implements OnInit {
  isSaving = false;

  responsableRestaurantsSharedCollection: IResponsableRestaurant[] = [];

  editForm = this.fb.group({
    id: [],
    nomRestaurant: [],
    adresseRestaurant: [],
    numRestaurant: [],
    dateOuverture: [],
    dateFermiture: [],
    ResponsableRestaurant: [],
  });

  constructor(
    protected restaurantService: RestaurantService,
    protected responsableRestaurantService: ResponsableRestaurantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurant }) => {
      if (restaurant.id === undefined) {
        const today = dayjs().startOf('day');
        restaurant.dateOuverture = today;
        restaurant.dateFermiture = today;
      }

      this.updateForm(restaurant);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const restaurant = this.createFromForm();
    if (restaurant.id !== undefined) {
      this.subscribeToSaveResponse(this.restaurantService.update(restaurant));
    } else {
      this.subscribeToSaveResponse(this.restaurantService.create(restaurant));
    }
  }

  trackResponsableRestaurantById(index: number, item: IResponsableRestaurant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRestaurant>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(restaurant: IRestaurant): void {
    this.editForm.patchValue({
      id: restaurant.id,
      nomRestaurant: restaurant.nomRestaurant,
      adresseRestaurant: restaurant.adresseRestaurant,
      numRestaurant: restaurant.numRestaurant,
      dateOuverture: restaurant.dateOuverture ? restaurant.dateOuverture.format(DATE_TIME_FORMAT) : null,
      dateFermiture: restaurant.dateFermiture ? restaurant.dateFermiture.format(DATE_TIME_FORMAT) : null,
      ResponsableRestaurant: restaurant.responsableRestaurant,
    });

    this.responsableRestaurantsSharedCollection = this.responsableRestaurantService.addResponsableRestaurantToCollectionIfMissing(
      this.responsableRestaurantsSharedCollection,
      restaurant.responsableRestaurant
    );
  }

  protected loadRelationshipsOptions(): void {
    this.responsableRestaurantService
      .query()
      .pipe(map((res: HttpResponse<IResponsableRestaurant[]>) => res.body ?? []))
      .pipe(
        map((responsableRestaurants: IResponsableRestaurant[]) =>
          this.responsableRestaurantService.addResponsableRestaurantToCollectionIfMissing(
            responsableRestaurants,
            this.editForm.get('ResponsableRestaurant')!.value
          )
        )
      )
      .subscribe(
        (responsableRestaurants: IResponsableRestaurant[]) => (this.responsableRestaurantsSharedCollection = responsableRestaurants)
      );
  }

  protected createFromForm(): IRestaurant {
    return {
      ...new Restaurant(),
      id: this.editForm.get(['id'])!.value,
      nomRestaurant: this.editForm.get(['nomRestaurant'])!.value,
      adresseRestaurant: this.editForm.get(['adresseRestaurant'])!.value,
      numRestaurant: this.editForm.get(['numRestaurant'])!.value,
      dateOuverture: this.editForm.get(['dateOuverture'])!.value
        ? dayjs(this.editForm.get(['dateOuverture'])!.value, DATE_TIME_FORMAT)
        : undefined,
      dateFermiture: this.editForm.get(['dateFermiture'])!.value
        ? dayjs(this.editForm.get(['dateFermiture'])!.value, DATE_TIME_FORMAT)
        : undefined,
      responsableRestaurant: this.editForm.get(['ResponsableRestaurant'])!.value,
    };
  }
}
