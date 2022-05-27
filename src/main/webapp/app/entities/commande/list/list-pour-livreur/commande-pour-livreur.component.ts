import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ClientService } from 'app/entities/client/service/client.service';
import { ICommande } from '../../commande.model';
import { CommandeService } from '../../service/commande.service';
import { listDetailsCommandeComponent } from '../list-details-commande/list-details-commande';
import Swal from 'sweetalert2';
import { ILivreur } from 'app/entities/livreur/livreur.model';

@Component({
  selector: 'jhi-commande-pour-livreur',
  templateUrl: './commande-pour-livreur.component.html',
})
export class CommandePourlivreurComponent implements OnInit {
  commandes?: ICommande[] = [];
  livreur!: ILivreur;
  parent!: any;
  isLoading = false;
  page?: number;
  modalRef: any;

  constructor(protected clientService: ClientService, protected commandeService: CommandeService, protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;

    this.commandeService.query({
      'livreurId.specified': false,
      size: 1000
    }).subscribe({
      next: (res: HttpResponse<ICommande[]>) => {
        this.isLoading = false;
        this.commandes = res.body ?? [];
        this.commandes.forEach(cmd => {
          this.clientService.find(cmd.client!.id!).subscribe(resClient => cmd.client = resClient.body!)
        });
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }
  listCommande(commande: ICommande): void {
    this.modalRef = this.modalService.open(listDetailsCommandeComponent as Component, { size: 'lg' });
    this.modalRef.componentInstance.parent = this;
    this.modalRef.componentInstance.commande = commande!;
  }

  modifierEtatCmd(cmd: ICommande): void {
    Swal.fire({
      title: "Modifier l'etat du commande",
      html:
        '  <strong></strong> ... <br/><br/>' +
        '<button id="accepte" class="btn btn-success text-white">acceptée</button><br /><br />' +
        '',
      onBeforeOpen: () => {
        const content = Swal.getContent();
        const $ = content.querySelector.bind(content);

        const accepte = $('#accepte');

        Swal.showLoading();

        function toggleButtons(): void {
          Swal.close();
        }

        accepte!.addEventListener('click', () => {
          cmd.etat = 'accepte';
          cmd.livreur = this.livreur!;
          this.commandeService.update(cmd).subscribe(() => {
            this.loadAll()
            this.parent.loadPage();
          });
          toggleButtons();
        });
      },
    });
  }
}
