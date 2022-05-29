import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommande } from '../../entities/commande/commande.model';

import { CommandeService } from '../../entities/commande/service/commande.service';
import { ClientService } from 'app/entities/client/service/client.service';
import { IClient } from 'app/entities/client/client.model';
import { listDetailsCommandeComponent } from 'app/entities/commande/list/list-details-commande/list-details-commande';
import Swal from 'sweetalert2';
import * as dayjs from 'dayjs';

@Component({
  selector: 'jhi-commande-pour-client',
  templateUrl: './commande-pour-client.component.html',
})
export class CommandePourClientComponent implements OnInit {
  commandes?: ICommande[];
  client: IClient = {};
  parent!: any;
  isLoading = false;
  page?: number;
  modalRef: any;
  Swals2: any;

  constructor(protected clientService: ClientService, protected commandeService: CommandeService, protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;

    this.commandeService.query({
      'clientId.equals': this.client.id,
      size: 500,
    }).subscribe({
      next: (res: HttpResponse<ICommande[]>) => {
        this.isLoading = false;
        this.commandes = res.body ?? [];
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
    this.modalRef.componentInstance.disableRoleEdit = true;
  }

  editDateLivraison(cmd: ICommande): void {
    Swal.fire({
      title: "Modifier l'etat du commande",
      html:
        `<input type="datetime-local" class="form-control" id="date"
          formControlName="dateLivraison" placeholder="YYYY-MM-DD HH:mm"
        />` +
        '<button id="changer" class="btn btn-success text-white">changer</button><br /><br />' +
        '',
        showConfirmButton: false,
      onBeforeOpen: () => {
        const content = Swal.getContent();
        const $ = content.querySelector.bind(content);

        const changer = $('#changer');

        function toggleButtons(): void {
          Swal.close();
        }

        changer!.addEventListener('click', () => {
          cmd.dateSortie = dayjs((document.getElementById('date') as HTMLInputElement).value);
          this.commandeService.update(cmd).subscribe(() => this.loadAll());
          toggleButtons();
        });
      },
    });
  }
}
