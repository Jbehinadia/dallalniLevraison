import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IResponsableRestaurant, ResponsableRestaurant } from '../responsable-restaurant.model';
import { ResponsableRestaurantService } from '../service/responsable-restaurant.service';

@Component({
  selector: 'jhi-responsable-restaurant-update',
  templateUrl: './responsable-restaurant-update.component.html',
})
export class ResponsableRestaurantUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nomResponsable: [],
    prenomResponsable: [],
    adresseResponsable: [],
    numResponsable: [],
  });

  constructor(
    protected responsableRestaurantService: ResponsableRestaurantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ responsableRestaurant }) => {
      this.updateForm(responsableRestaurant);
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
    });
  }

  protected createFromForm(): IResponsableRestaurant {
    return {
      ...new ResponsableRestaurant(),
      id: this.editForm.get(['id'])!.value,
      nomResponsable: this.editForm.get(['nomResponsable'])!.value,
      prenomResponsable: this.editForm.get(['prenomResponsable'])!.value,
      adresseResponsable: this.editForm.get(['adresseResponsable'])!.value,
      numResponsable: this.editForm.get(['numResponsable'])!.value,
    };
  }
}
