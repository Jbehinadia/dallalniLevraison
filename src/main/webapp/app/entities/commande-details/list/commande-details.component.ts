import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommandeDetails } from '../commande-details.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { CommandeDetailsService } from '../service/commande-details.service';
import { CommandeDetailsDeleteDialogComponent } from '../delete/commande-details-delete-dialog.component';
import Swal from 'sweetalert2';

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

  constructor(
    protected commandeDetailsService: CommandeDetailsService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.commandeDetailsService
      .query({
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

  ngOnInit(): void {
    this.handleNavigation();
  }

  trackId(_index: number, item: ICommandeDetails): number {
    return item.id!;
  }

  modifierEtatCmd(cmd: ICommandeDetails): void {
    Swal.fire({
      title: "Modifier l'etat du commande",
      html:
        '  <strong></strong> ... <br/><br/>' +
        '<button id="reprise" class="btn btn-info text-white">reprise</button><br /><br />' +
        '<button id="annule" class="btn btn-danger text-white">annulée</button><br /><br />' +
        '<button id="accepte" class="btn btn-success text-white">acceptée</button><br /><br />' +
        '<button id="demande" class="btn btn-secondary text-white">demandée</button><br /><br />' +
        '<button id="prepare" class="btn btn-warning text-white">preparée</button><br /><br />' +
        '<button id="livre" class="btn btn-success text-white">livrée</button><br /><br />' +
        '',
      onBeforeOpen: () => {
        const content = Swal.getContent();
        const $ = content.querySelector.bind(content);

        const reprise = $('#reprise');
        const annule = $('#annule');
        const accepte = $('#accepte');
        const demande = $('#demande');
        const prepare = $('#prepare');
        const livre = $('#livre');

        Swal.showLoading();

        function toggleButtons(): void {
          Swal.close();
        }

        reprise!.addEventListener('click', () => {
          cmd.etat = 'reprise';
          this.commandeDetailsService.update(cmd).subscribe();
          toggleButtons();
        });

        annule!.addEventListener('click', () => {
          cmd.etat = 'annule';
          this.commandeDetailsService.update(cmd).subscribe();
          toggleButtons();
        });

        accepte!.addEventListener('click', () => {
          cmd.etat = 'accepte';
          this.commandeDetailsService.update(cmd).subscribe();
          toggleButtons();
        });

        demande!.addEventListener('click', () => {
          cmd.etat = 'demande';
          this.commandeDetailsService.update(cmd).subscribe();
          toggleButtons();
        });

        prepare!.addEventListener('click', () => {
          cmd.etat = 'prepare';
          this.commandeDetailsService.update(cmd).subscribe();
          toggleButtons();
        });

        livre!.addEventListener('click', () => {
          cmd.etat = 'livre';
          this.commandeDetailsService.update(cmd).subscribe();
          toggleButtons();
        });
      },
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
    this.commandeDetails = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
