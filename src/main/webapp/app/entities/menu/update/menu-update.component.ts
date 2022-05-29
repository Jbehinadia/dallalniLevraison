import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMenu, Menu } from '../menu.model';
import { MenuService } from '../service/menu.service';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-menu-update',
  templateUrl: './menu-update.component.html',
})
export class MenuUpdateComponent implements OnInit {
  isSaving = false;

  restaurantsSharedCollection: IRestaurant[] = [];

  editForm = this.fb.group({
    id: [],
    nomMenu: [],
    restaurant: [],
  });
  restau: IRestaurant = {};

  constructor(
    protected menuService: MenuService,
    protected restaurantService: RestaurantService,
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ menu }) => {
      this.updateForm(menu);

      this.accountService.getAuthenticationState().subscribe(account => {
        if(account!.responsable!) {
          this.getRestau(account!);
        } else {
          this.loadRelationshipsOptions();
        }
      });
    });
  }
  getRestau(account: Account): void {
    this.restaurantService
    .query({
      'responsableRestaurantId.equals': account.responsable!
    }).subscribe((resRestau: HttpResponse<IRestaurant[]>) => {
      this.restau = resRestau.body![0];
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const menu = this.createFromForm();
    if (menu.id !== undefined) {
      this.subscribeToSaveResponse(this.menuService.update(menu));
    } else {
      this.subscribeToSaveResponse(this.menuService.create(menu));
    }
  }

  trackRestaurantById(_index: number, item: IRestaurant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMenu>>): void {
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

  protected updateForm(menu: IMenu): void {
    this.editForm.patchValue({
      id: menu.id,
      nomMenu: menu.nomMenu,
      restaurant: menu.restaurant,
    });

    this.restaurantsSharedCollection = this.restaurantService.addRestaurantToCollectionIfMissing(
      this.restaurantsSharedCollection,
      menu.restaurant
    );
  }

  protected loadRelationshipsOptions(): void {
    let query = {}
    if (this.restau.id) {query = { ...query, ...{ 'id.equals': this.restau.id } };}
    this.restaurantService
      .query({...query})
      .pipe(map((res: HttpResponse<IRestaurant[]>) => res.body ?? []))
      .pipe(
        map((restaurants: IRestaurant[]) =>
          this.restaurantService.addRestaurantToCollectionIfMissing(restaurants, this.editForm.get('restaurant')!.value)
        )
      )
      .subscribe((restaurants: IRestaurant[]) => (this.restaurantsSharedCollection = restaurants));
  }

  protected createFromForm(): IMenu {
    return {
      ...new Menu(),
      id: this.editForm.get(['id'])!.value,
      nomMenu: this.editForm.get(['nomMenu'])!.value,
      restaurant: this.editForm.get(['restaurant'])!.value,
    };
  }
}
