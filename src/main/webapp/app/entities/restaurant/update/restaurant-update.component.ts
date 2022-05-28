import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { IRestaurant } from '../restaurant.model';
import { RestaurantService } from '../service/restaurant.service';
import { ResponsableRestaurantService } from 'app/entities/responsable-restaurant/service/responsable-restaurant.service';
import { IResponsableRestaurant } from 'app/entities/responsable-restaurant/responsable-restaurant.model';

@Component({
  selector: 'jhi-restaurant-update',
  templateUrl: './restaurant-update.component.html',
})
export class RestaurantUpdateComponent implements OnInit {
  isSaving = false;
  heureO!: string;
  heureF!: string;
  restaurant: IRestaurant = {};
  responsables: IResponsableRestaurant[] = [];

  constructor(
    protected responsableRestaurantService: ResponsableRestaurantService,
    protected restaurantService: RestaurantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurant }) => {
      this.restaurant = restaurant;
      this.loadAllResponsables();
      if (!restaurant.id) {
        this.heureO = dayjs(new Date()).format('HH:mm');
        this.heureF = dayjs(new Date()).format('HH:mm');
      } else {
        this.heureO = dayjs(this.restaurant.dateOuverture).format('HH:mm');
        this.heureF = dayjs(this.restaurant.dateFermiture).format('HH:mm');
      }
    });
  }

  loadAllResponsables(): void {
    this.responsableRestaurantService
      .query({
        size: 100
      })
      .subscribe((resResp: HttpResponse<IResponsableRestaurant[]>) => this.responsables = resResp.body!);
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    if (this.heureO) {
      const h = Number(this.heureO.toString().split(':')[0]);
      const m = Number(this.heureO.toString().split(':')[1]);
      const dateOuverture = new Date();
      dateOuverture.setHours(h);
      dateOuverture.setMinutes(m);
      this.restaurant.dateOuverture = dayjs(dateOuverture);
    }
    if (this.heureF) {
      const h = Number(this.heureF.toString().split(':')[0]);
      const m = Number(this.heureF.toString().split(':')[1]);
      const dateFermiture = new Date();
      dateFermiture.setHours(h);
      dateFermiture.setMinutes(m);
      this.restaurant.dateFermiture = dayjs(dateFermiture);
    }

    if (this.restaurant.id !== undefined) {
      this.subscribeToSaveResponse(this.restaurantService.update(this.restaurant));
    } else {
      this.subscribeToSaveResponse(this.restaurantService.create(this.restaurant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRestaurant>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
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
}
