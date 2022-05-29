import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import Swal from 'sweetalert2';
import { ICommandeDetails } from 'app/entities/commande-details/commande-details.model';
import { CommandeDetailsService } from 'app/entities/commande-details/service/commande-details.service';
import { ICommande } from '../../commande.model';
import { PlatService } from 'app/entities/plat/service/plat.service';
import { MenuService } from 'app/entities/menu/service/menu.service';
import { IPlat } from 'app/entities/plat/plat.model';
import { map, mergeMap } from 'rxjs/operators';
import { IMenu } from 'app/entities/menu/menu.model';

@Component({
  selector: 'jhi-list-details-commande',
  templateUrl: './list-details-commande.html',
})
export class listDetailsCommandeComponent implements OnInit {
  commandeDetails?: ICommandeDetails[] = [];
  isLoading = false;
  commande!: ICommande;
  parent!: any;
  disableRoleEdit?: boolean;

  constructor(
    protected commandeDetailsService: CommandeDetailsService,
    protected platService: PlatService,
    protected menuService: MenuService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(): void {
    this.isLoading = true;

    this.commandeDetailsService.query({
      'commandeId.equals': this.commande.id,
      size: 100
    }).subscribe({
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
    if(!this.disableRoleEdit) {
      Swal.fire({
        title: "Modifier l'etat du commande",
        html:
          '  <strong></strong> ... <br/><br/>' +
          '<button id="envoyée" class="btn btn-info text-white">envoyée</button><br /><br />' +
          '<button id="annule" class="btn btn-danger text-white">annulée</button><br /><br />' +
          '<button id="accepte" class="btn btn-success text-white">acceptée</button><br /><br />' +
          '<button id="demande" class="btn btn-secondary text-white">demandée</button><br /><br />' +
          '<button id="prepare" class="btn btn-warning text-white">preparée</button><br /><br />' +
          '<button id="livre" class="btn btn-success text-white">livrée</button><br /><br />' +
          '',
        onBeforeOpen: () => {
          const content = Swal.getContent();
          const $ = content.querySelector.bind(content);

          const envoyée = $('#envoyée');
          const annule = $('#annule');
          const accepte = $('#accepte');
          const demande = $('#demande');
          const prepare = $('#prepare');
          const livre = $('#livre');

          Swal.showLoading();

          function toggleButtons(): void {
            Swal.close();
          }

          envoyée!.addEventListener('click', () => {
            cmd.etat = 'envoyée';
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
  }

  protected onSuccess(data: ICommandeDetails[]): void {
    this.commandeDetails = data;
    this.commandeDetails.forEach(element => {
      this.platService.find(element.plat!.id!).pipe(
        map((res: HttpResponse<IPlat>) => res.body!),
        mergeMap((resPlat: IPlat) => 
          this.menuService.find(resPlat.menu!.id!).pipe(map(res => res.body!))
        )
      ).subscribe((resMenu: IMenu) => element.Restau = resMenu.restaurant!)
    });
  }
}
