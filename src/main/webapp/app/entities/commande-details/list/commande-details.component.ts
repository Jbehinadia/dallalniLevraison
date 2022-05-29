import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommandeDetails } from '../commande-details.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { CommandeDetailsService } from '../service/commande-details.service';
import { CommandeDetailsDeleteDialogComponent } from '../delete/commande-details-delete-dialog.component';
import Swals2 from 'sweetalert2';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { map, mergeMap } from 'rxjs/operators';
import { ICommande } from 'app/entities/commande/commande.model';
import { PlatService } from 'app/entities/plat/service/plat.service';
import { MenuService } from 'app/entities/menu/service/menu.service';
import { IPlat } from 'app/entities/plat/plat.model';
import { IMenu } from 'app/entities/menu/menu.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { ResponsableRestaurantService } from 'app/entities/responsable-restaurant/service/responsable-restaurant.service';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

@Component({
  selector: 'jhi-commande-details',
  templateUrl: './commande-details.component.html',
})
export class CommandeDetailsComponent implements OnInit {
  commandeDetails?: ICommandeDetails[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  restau: IRestaurant = {};

  constructor(
    protected restaurantService: RestaurantService,
    protected responsableRestaurantService: ResponsableRestaurantService,
    protected commandeDetailsService: CommandeDetailsService,
    protected commandeService: CommandeService,
    protected restauraService: CommandeService,
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected platService: PlatService,
    protected menuService: MenuService,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.getRestaurant(account!);
    });
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.commandeDetailsService
      .query({
        'etat.equals': 'demande',
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ICommandeDetails[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  getRestaurant(account: Account): void {
      this.restaurantService
      .query({
        'responsableRestaurantId.equals': account.responsable!
      }).subscribe((resRestau: HttpResponse<IRestaurant[]>) => {
        this.restau = resRestau.body![0];
        this.handleNavigation();
        this.getMenu();
      });
  }

  getMenu(): void {
    this.menuService
    .query({
      'restaurantId.equals': this.restau.id!,
      sort: ['id,desc']
    }).subscribe((resRestau: HttpResponse<IMenu[]>) => {
      this.restau.actuelMenu = resRestau.body![0].nomMenu!;
    });
  }

  trackId(_index: number, item: ICommandeDetails): number {
    return item.id!;
  }

  modifierEtatCmd(cmd: ICommandeDetails): void {
    Swals2.fire({
      title: "vous êtes certaine?",
      html: 'vous terminez cette commande!',
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ff8200',
    }).then(res => {
      if (res.value) {
        cmd.etat = 'prepare';
        this.commandeDetailsService.update(cmd).subscribe(() => this.loadPage());
      }
    });
  }

  delete(commandeDetails: ICommandeDetails): void {
    const modalRef = this.modalService.open(CommandeDetailsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.commandeDetails = commandeDetails;
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

  protected onSuccess(data: ICommandeDetails[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/commande-details'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.commandeDetails = [];
    data!.forEach(element => {
      this.platService.find(element.plat!.id!).pipe(
        map((res: HttpResponse<IPlat>) => res.body!),
        mergeMap((resPlat: IPlat) => 
          this.menuService.find(resPlat.menu!.id!).pipe(map(res => res.body!))
        )
      ).subscribe((resMenu: IMenu) => {
        if(resMenu.restaurant && resMenu.restaurant.id === this.restau.id) {
          element.Restau = resMenu.restaurant!;
          this.commandeDetails!.push(element);
          this.commandeService.find(element.commande!.id!).pipe(map(res => res.body!)).subscribe((resCmd: ICommande) => 
            element.livreur = resCmd.livreur!
          )
        }
      })
    });
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
