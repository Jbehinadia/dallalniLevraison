import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IResponsableRestaurant, ResponsableRestaurant } from '../responsable-restaurant.model';
import { ResponsableRestaurantService } from '../service/responsable-restaurant.service';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

@Component({
  selector: 'jhi-responsable-restaurant-update',
  templateUrl: './responsable-restaurant-update.component.html',
})
export class ResponsableRestaurantUpdateComponent implements OnInit {
  isSaving = false;

  restaurantsCollection: IRestaurant[] = [];

  editForm = this.fb.group({
    id: [],
    nomResponsable: [],
    prenomResponsable: [],
    adresseResponsable: [],
    numResponsable: [],
    restaurant: [],
  });

  constructor(
    protected responsableRestaurantService: ResponsableRestaurantService,
    protected restaurantService: RestaurantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ responsableRestaurant }) => {
      this.updateForm(responsableRestaurant);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const responsableRestaurant = this.createFromForm();
    if (responsableRestaurant.id !== undefined) {
      this.subscribeToSaveResponse(this.responsableRestaurantService.update(responsableRestaurant));
    } else {
      this.subscribeToSaveResponse(this.responsableRestaurantService.create(responsableRestaurant));
    }
  }

  trackRestaurantById(index: number, item: IRestaurant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResponsableRestaurant>>): void {
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

  protected updateForm(responsableRestaurant: IResponsableRestaurant): void {
    this.editForm.patchValue({
      id: responsableRestaurant.id,
      nomResponsable: responsableRestaurant.nomResponsable,
      prenomResponsable: responsableRestaurant.prenomResponsable,
      adresseResponsable: responsableRestaurant.adresseResponsable,
      numResponsable: responsableRestaurant.numResponsable,
      restaurant: responsableRestaurant.restaurant,
    });

    this.restaurantsCollection = this.restaurantService.addRestaurantToCollectionIfMissing(
      this.restaurantsCollection,
      responsableRestaurant.restaurant
    );
  }

  protected loadRelationshipsOptions(): void {
    this.restaurantService
      .query({ filter: 'responsablerestaurant-is-null' })
      .pipe(map((res: HttpResponse<IRestaurant[]>) => res.body ?? []))
      .pipe(
        map((restaurants: IRestaurant[]) =>
          this.restaurantService.addRestaurantToCollectionIfMissing(restaurants, this.editForm.get('restaurant')!.value)
        )
      )
      .subscribe((restaurants: IRestaurant[]) => (this.restaurantsCollection = restaurants));
  }

  protected createFromForm(): IResponsableRestaurant {
    return {
      ...new ResponsableRestaurant(),
      id: this.editForm.get(['id'])!.value,
      nomResponsable: this.editForm.get(['nomResponsable'])!.value,
      prenomResponsable: this.editForm.get(['prenomResponsable'])!.value,
      adresseResponsable: this.editForm.get(['adresseResponsable'])!.value,
      numResponsable: this.editForm.get(['numResponsable'])!.value,
      restaurant: this.editForm.get(['restaurant'])!.value,
    };
  }
}
