import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import Swal from 'sweetalert2';
import { ICommandeDetails } from 'app/entities/commande-details/commande-details.model';
import { CommandeDetailsService } from 'app/entities/commande-details/service/commande-details.service';
import { ICommande } from '../../commande.model';

@Component({
  selector: 'jhi-list-details-commande',
  templateUrl: './list-details-commande.html',
})
export class listDetailsCommandeComponent implements OnInit {
  commandeDetails?: ICommandeDetails[] = [];
  isLoading = false;
  commande!: ICommande;
  parent!: any;

  constructor(
    protected commandeDetailsService: CommandeDetailsService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(): void {
    this.isLoading = true;

    this.commandeDetailsService.query({}).subscribe({
      next: (res: HttpResponse<ICommandeDetails[]>) => {
        this.isLoading = false;
        this.onSuccess(res.body!);
      },
      error: () => {
        this.isLoading = false;
      },
    });
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

  protected onSuccess(data: ICommandeDetails[]): void {
    this.commandeDetails = data;
  }
}
