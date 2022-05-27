import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommande } from '../commande.model';
import { CommandeService } from '../service/commande.service';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import Swals2 from 'sweetalert2';
import Swal from 'sweetalert2';
import { LivreurService } from 'app/entities/livreur/service/livreur.service';
import { ILivreur } from 'app/entities/livreur/livreur.model';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { ClientService } from 'app/entities/client/service/client.service';
import { CommandePourlivreurComponent } from './list-pour-livreur/commande-pour-livreur.component';
import { listDetailsCommandeComponent } from './list-details-commande/list-details-commande';

@Component({
  selector: 'jhi-commande',
  templateUrl: './commande.component.html',
})
export class CommandeComponent implements OnInit {
  commandes?: ICommande[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  livreur: ILivreur = {};
  modalRef: any;

  constructor(
    protected clientService: ClientService,
    protected commandeService: CommandeService,
    protected livreurService: LivreurService,
    protected activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.getLivreur(account!);
    });
  }

  getLivreur(account: Account): void {
    this.livreurService.find(account.livreur!).subscribe((resLivreur: HttpResponse<ILivreur>) => {
      this.livreur = resLivreur.body!;
      this.handleNavigation();
    });
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.commandeService
      .query({
        'livreurId.equals': this.livreur.id,
        'etat.in': ['accepte', 'demande', 'prepare'],
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ICommande[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  trackId(_index: number, item: ICommande): number {
    return item.id!;
  }

  associerCommande(commande: ICommande): void {
    commande.livreur = this.livreur!;
    Swals2.fire({
      title: 'associer cette commande',
      text: 'vous êtes sûr de vouloir associer cette commande?',
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ff8200',
    }).then(() => this.commandeService.update(commande).subscribe());
  }

  delete(commande: ICommande): void {
    commande.livreur = {};
    Swals2.fire({
      title: 'dissocier cette commande',
      text: 'vous êtes sûr de vouloir dissocier cette commande?',
      type: 'error',
      showCancelButton: true,
      confirmButtonColor: '#ff8200',
    }).then(() => this.commandeService.update(commande).subscribe());
  }

  editPrixLivraison(cmd: ICommande): void {
    Swals2.fire({
      title: 'Modifier le Prix Livraison',
      input: 'number',
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      showCancelButton: true,
    }).then(res => {
      if (res.value) {
        cmd.prixLivreson = Number(res.value);
        this.commandeService.update(cmd).subscribe();
      }
    });
  }

  modifierEtatCmd(cmd: ICommande): void {
    Swal.fire({
      title: "Modifier l'etat du commande",
      html:
        '  <strong></strong> ... <br/><br/>' +
        '<button id="annule" class="btn btn-danger text-white">annulée</button><br /><br />' +
        '<button id="demande" class="btn btn-secondary text-white">demandée</button><br /><br />' +
        '<button id="prepare" class="btn btn-warning text-white">preparée</button><br /><br />' +
        '<button id="livre" class="btn btn-success text-white">livrée</button><br /><br />' +
        '',
      onBeforeOpen: () => {
        const content = Swal.getContent();
        const $ = content.querySelector.bind(content);

        const annule = $('#annule');
        const demande = $('#demande');
        const prepare = $('#prepare');
        const livre = $('#livre');

        Swal.showLoading();

        function toggleButtons(): void {
          Swal.close();
        }

        annule!.addEventListener('click', () => {
          cmd.etat = 'annule';
          this.commandeService.update(cmd).subscribe(() => this.loadPage());
          toggleButtons();
        });

        demande!.addEventListener('click', () => {
          cmd.etat = 'demande';
          this.commandeService.update(cmd).subscribe(() => this.loadPage());
          toggleButtons();
        });

        prepare!.addEventListener('click', () => {
          cmd.etat = 'prepare';
          this.commandeService.update(cmd).subscribe(() => this.loadPage());
          toggleButtons();
        });

        livre!.addEventListener('click', () => {
          cmd.etat = 'livre';
          this.commandeService.update(cmd).subscribe(() => this.loadPage());
          toggleButtons();
        });
      },
    });
  }

  loadOtherCommandes(): void {
    this.modalRef = this.modalService.open(CommandePourlivreurComponent as Component, { size: 'lg' });
    this.modalRef.componentInstance.parent = this;
    this.modalRef.componentInstance.livreur = this.livreur!;
  }
  listCommande(commande: ICommande): void {
    this.modalRef = this.modalService.open(listDetailsCommandeComponent as Component, { size: 'lg' });
    this.modalRef.componentInstance.parent = this;
    this.modalRef.componentInstance.commande = commande!;
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

  protected onSuccess(data: ICommande[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/commande'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.commandes = data ?? [];
    this.commandes.forEach(cmd => {
      this.clientService.find(cmd.client!.id!).subscribe(res => (cmd.client = res.body!));
    });
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
