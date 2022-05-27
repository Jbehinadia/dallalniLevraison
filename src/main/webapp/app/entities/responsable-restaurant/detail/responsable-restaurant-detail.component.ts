import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IResponsableRestaurant } from '../responsable-restaurant.model';

@Component({
  selector: 'jhi-responsable-restaurant-detail',
  templateUrl: './responsable-restaurant-detail.component.html',
})
export class ResponsableRestaurantDetailComponent implements OnInit {
  responsableRestaurant: IResponsableRestaurant | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ responsableRestaurant }) => {
      this.responsableRestaurant = responsableRestaurant;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
