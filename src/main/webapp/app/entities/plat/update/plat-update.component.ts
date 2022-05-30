import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPlat, Plat } from '../plat.model';
import { PlatService } from '../service/plat.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IMenu } from 'app/entities/menu/menu.model';
import { MenuService } from 'app/entities/menu/service/menu.service';
import { ITypePlat } from 'app/entities/type-plat/type-plat.model';
import { TypePlatService } from 'app/entities/type-plat/service/type-plat.service';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { IRestaurant, Restaurant } from 'app/entities/restaurant/restaurant.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-plat-update',
  templateUrl: './plat-update.component.html',
})
export class PlatUpdateComponent implements OnInit {
  isSaving = false;

  menusSharedCollection: IMenu[] = [];
  typePlatsSharedCollection: ITypePlat[] = [];

  editForm = this.fb.group({
    id: [],
    nomPlat: [],
    imagePath: [],
    prix: [],
    remisePerc: [],
    remiceVal: [],
    menu: [],
    typePlat: [],
  });
  menu: IMenu = {};

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected platService: PlatService,
    protected menuService: MenuService,
    protected restaurantService: RestaurantService,
    protected typePlatService: TypePlatService,
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plat }) => {
      this.updateForm(plat);

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
      this.menuService
      .query({'restaurantId.equals': resRestau.body![0]!.id!, sort: ['id,desc']})
      .subscribe((resMenu: HttpResponse<IMenu[]>) => {
        this.menu = resMenu.body![0];
        this.loadRelationshipsOptions();
      })
    });
  }

  uploadFile(event: any): any {
    const reader = new FileReader();
    const file = event.target!.files[0];
    if (event.target!.files && event.target.files[0]) {
      reader.readAsDataURL(file);

      // When file uploads push it to file list
      reader.onload = () => {
        this.editForm.controls['imagePath'].setValue(reader.result!.toString());
      };
    }
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('dallalniDeliveryFoodApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const plat = this.createFromForm();
    if (plat.id !== undefined) {
      this.subscribeToSaveResponse(this.platService.update(plat));
    } else {
      this.subscribeToSaveResponse(this.platService.create(plat));
    }
  }

  trackMenuById(_index: number, item: IMenu): number {
    return item.id!;
  }

  trackTypePlatById(_index: number, item: ITypePlat): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlat>>): void {
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

  protected updateForm(plat: IPlat): void {
    this.editForm.patchValue({
      id: plat.id,
      nomPlat: plat.nomPlat,
      imagePath: plat.imagePath,
      prix: plat.prix,
      remisePerc: plat.remisePerc,
      remiceVal: plat.remiceVal,
      menu: plat.menu,
      typePlat: plat.typePlat,
    });

    this.menusSharedCollection = this.menuService.addMenuToCollectionIfMissing(this.menusSharedCollection, plat.menu);
    this.typePlatsSharedCollection = this.typePlatService.addTypePlatToCollectionIfMissing(this.typePlatsSharedCollection, plat.typePlat);
  }

  protected loadRelationshipsOptions(): void {
    let query = {}
    if (this.menu.id) {query = { ...query, ...{ 'id.equals': this.menu.id } };}
    this.menuService
      .query({...query})
      .pipe(map((res: HttpResponse<IMenu[]>) => res.body ?? []))
      .pipe(map((menus: IMenu[]) => this.menuService.addMenuToCollectionIfMissing(menus, this.editForm.get('menu')!.value)))
      .subscribe((menus: IMenu[]) => {
        this.menusSharedCollection = menus;
        this.menusSharedCollection.forEach(menu => {
          if (menu.restaurant!.id) {
            this.restaurantService.find(menu.restaurant!.id).subscribe((resRestau: HttpResponse<Restaurant>) => {
              if (resRestau.body) {
                menu.nomRestau = resRestau.body.nomRestaurant;
              }
            });
          }
        });
      });

    this.typePlatService
      .query()
      .pipe(map((res: HttpResponse<ITypePlat[]>) => res.body ?? []))
      .pipe(
        map((typePlats: ITypePlat[]) =>
          this.typePlatService.addTypePlatToCollectionIfMissing(typePlats, this.editForm.get('typePlat')!.value)
        )
      )
      .subscribe((typePlats: ITypePlat[]) => (this.typePlatsSharedCollection = typePlats));
  }

  protected createFromForm(): IPlat {
    return {
      ...new Plat(),
      id: this.editForm.get(['id'])!.value,
      nomPlat: this.editForm.get(['nomPlat'])!.value,
      imagePath: this.editForm.get(['imagePath'])!.value,
      prix: this.editForm.get(['prix'])!.value,
      remisePerc: this.editForm.get(['remisePerc'])!.value,
      remiceVal: this.editForm.get(['remiceVal'])!.value,
      menu: this.editForm.get(['menu'])!.value,
      typePlat: this.editForm.get(['typePlat'])!.value,
    };
  }
}
