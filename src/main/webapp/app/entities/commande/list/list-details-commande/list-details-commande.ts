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
import { AccountService } from 'app/core/auth/account.service';

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
  siResto = false;
  siLivreur = false;

  constructor(
    protected commandeDetailsService: CommandeDetailsService,
    protected platService: PlatService,
    protected menuService: MenuService,
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.siResto = this.accountService.hasAnyAuthority(['ROLE_Resto']);
    this.siLivreur = this.accountService.hasAnyAuthority(['ROLE_Livreur']);
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
        html:'<button id="dateLivraison" class="btn btn-success text-white"><br />',
        onBeforeOpen: () => {
          const content = Swal.getContent();
          const $ = content.querySelector.bind(content);

          const demande = $('#demande');
          const livre = $('#livre');

          demande!.addEventListener('click', () => {
            cmd.etat = 'demande';
            this.commandeDetailsService.update(cmd).subscribe();
            toggleButtons();
          });

          livre!.addEventListener('click', () => {
            cmd.etat = 'livre';
            this.commandeDetailsService.update(cmd).subscribe();
            toggleButtons();
          });

          Swal.showLoading();

          function toggleButtons(): void {
            Swal.close();
          }
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
