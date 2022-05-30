import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlat } from '../plat.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { PlatService } from '../service/plat.service';
import { PlatDeleteDialogComponent } from '../delete/plat-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';
import { MenuService } from 'app/entities/menu/service/menu.service';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { IMenu } from 'app/entities/menu/menu.model';
import { IRestaurant, Restaurant } from 'app/entities/restaurant/restaurant.model';
import { TypePlatService } from 'app/entities/type-plat/service/type-plat.service';
import { TypePlat } from 'app/entities/type-plat/type-plat.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-plat',
  templateUrl: './plat.component.html',
})
export class PlatComponent implements OnInit {
  plats?: IPlat[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  menu: IMenu = {};

  constructor(
    protected platService: PlatService,
    protected typePlatService: TypePlatService,
    protected menuService: MenuService,
    protected restaurantService: RestaurantService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: DataUtils,
    protected accountService: AccountService,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      if(account!.responsable!) {
        this.getRestau(account!);
      } else {
        this.handleNavigation();
      }
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
        this.handleNavigation();
      })
    });
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    let query = {}
    if (this.menu.id) {query = { ...query, ...{ 'menuId.equals': this.menu.id } };}
    this.platService
      .query({
        ...query,
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IPlat[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  trackId(_index: number, item: IPlat): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(plat: IPlat): void {
    const modalRef = this.modalService.open(PlatDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.plat = plat;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: IPlat[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/plat'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.plats = data ?? [];
    this.plats.forEach(plat => {
      if (plat.typePlat!.id) {
        this.typePlatService.find(plat.typePlat!.id).subscribe((resType: HttpResponse<TypePlat>) => {
          plat.typePlat!.type = resType.body?.type;
        });
      }

      this.menuService.find(plat.menu!.id!).subscribe((resMenu: HttpResponse<IMenu>) => {
        plat.menu!.nomMenu = resMenu.body!.nomMenu;
        if (resMenu.body!.restaurant!.id) {
          this.restaurantService.find(resMenu.body!.restaurant!.id).subscribe((resRestau: HttpResponse<Restaurant>) => {
            plat.nomRestau = resRestau.body?.nomRestaurant;
          });
        }
      });
    });
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
