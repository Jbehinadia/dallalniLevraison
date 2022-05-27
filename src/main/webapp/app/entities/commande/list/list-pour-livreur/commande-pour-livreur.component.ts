import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ClientService } from 'app/entities/client/service/client.service';
import { IClient } from 'app/entities/client/client.model';
import { ICommande } from '../../commande.model';
import { CommandeService } from '../../service/commande.service';
import { listDetailsCommandeComponent } from '../list-details-commande/list-details-commande';

@Component({
  selector: 'jhi-commande-pour-livreur',
  templateUrl: './commande-pour-livreur.component.html',
})
export class CommandePourlivreurComponent implements OnInit {
  commandes?: ICommande[] = [];
  client: IClient = {};
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

    this.commandeService.query({}).subscribe({
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
  }
}
