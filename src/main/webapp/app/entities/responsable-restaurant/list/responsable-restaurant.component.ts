import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IResponsableRestaurant } from '../responsable-restaurant.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { ResponsableRestaurantService } from '../service/responsable-restaurant.service';
import { ResponsableRestaurantDeleteDialogComponent } from '../delete/responsable-restaurant-delete-dialog.component';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

@Component({
  selector: 'jhi-responsable-restaurant',
  templateUrl: './responsable-restaurant.component.html',
})
export class ResponsableRestaurantComponent implements OnInit {
  responsableRestaurants?: IResponsableRestaurant[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected responsableRestaurantService: ResponsableRestaurantService,
    protected restaurantService: RestaurantService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.responsableRestaurantService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IResponsableRestaurant[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  ngOnInit(): void {
    this.handleNavigation();
  }

  trackId(_index: number, item: IResponsableRestaurant): number {
    return item.id!;
  }

  delete(responsableRestaurant: IResponsableRestaurant): void {
    const modalRef = this.modalService.open(ResponsableRestaurantDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.responsableRestaurant = responsableRestaurant;
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

  protected onSuccess(data: IResponsableRestaurant[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/responsable-restaurant'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.responsableRestaurants = data ?? [];
    this.responsableRestaurants.forEach(resp => {
      this.restaurantService
      .query({
        'responsableRestaurantId.equals': resp.id!
      }).subscribe((resRestau: HttpResponse<IRestaurant[]>) => {
        resp.restaurant = resRestau.body![0];
      });
    });
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
