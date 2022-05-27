import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICommandeDetails } from '../commande-details.model';

@Component({
  selector: 'jhi-commande-details-detail',
  templateUrl: './commande-details-detail.component.html',
})
export class CommandeDetailsDetailComponent implements OnInit {
  commandeDetails: ICommandeDetails | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commandeDetails }) => {
      this.commandeDetails = commandeDetails;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
