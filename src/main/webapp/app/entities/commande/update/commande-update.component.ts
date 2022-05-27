import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICommande, Commande } from '../commande.model';
import { CommandeService } from '../service/commande.service';
import { ILivreur } from 'app/entities/livreur/livreur.model';
import { LivreurService } from 'app/entities/livreur/service/livreur.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';

@Component({
  selector: 'jhi-commande-update',
  templateUrl: './commande-update.component.html',
})
export class CommandeUpdateComponent implements OnInit {
  isSaving = false;

  livreursSharedCollection: ILivreur[] = [];
  clientsSharedCollection: IClient[] = [];

  editForm = this.fb.group({
    id: [],
    adresseCommande: [],
    etat: [],
    dateCommande: [],
    prixTotal: [],
    remisePerc: [],
    remiceVal: [],
    prixLivreson: [],
    dateSortie: [],
    livreur: [],
    client: [],
  });

  constructor(
    protected commandeService: CommandeService,
    protected livreurService: LivreurService,
    protected clientService: ClientService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commande }) => {
      if (commande.id === undefined) {
        const today = dayjs().startOf('day');
        commande.dateCommande = today;
        commande.dateSortie = today;
      }

      this.updateForm(commande);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const commande = this.createFromForm();
    if (commande.id !== undefined) {
      this.subscribeToSaveResponse(this.commandeService.update(commande));
    } else {
      this.subscribeToSaveResponse(this.commandeService.create(commande));
    }
  }

  trackLivreurById(index: number, item: ILivreur): number {
    return item.id!;
  }

  trackClientById(index: number, item: IClient): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICommande>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(commande: ICommande): void {
    this.editForm.patchValue({
      id: commande.id,
      adresseCommande: commande.adresseCommande,
      etat: commande.etat,
      dateCommande: commande.dateCommande ? commande.dateCommande.format(DATE_TIME_FORMAT) : null,
      prixTotal: commande.prixTotal,
      remisePerc: commande.remisePerc,
      remiceVal: commande.remiceVal,
      prixLivreson: commande.prixLivreson,
      dateSortie: commande.dateSortie ? commande.dateSortie.format(DATE_TIME_FORMAT) : null,
      livreur: commande.livreur,
      client: commande.client,
    });

    this.livreursSharedCollection = this.livreurService.addLivreurToCollectionIfMissing(this.livreursSharedCollection, commande.livreur);
    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing(this.clientsSharedCollection, commande.client);
  }

  protected loadRelationshipsOptions(): void {
    this.livreurService
      .query()
      .pipe(map((res: HttpResponse<ILivreur[]>) => res.body ?? []))
      .pipe(
        map((livreurs: ILivreur[]) => this.livreurService.addLivreurToCollectionIfMissing(livreurs, this.editForm.get('livreur')!.value))
      )
      .subscribe((livreurs: ILivreur[]) => (this.livreursSharedCollection = livreurs));

    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing(clients, this.editForm.get('client')!.value)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));
  }

  protected createFromForm(): ICommande {
    return {
      ...new Commande(),
      id: this.editForm.get(['id'])!.value,
      adresseCommande: this.editForm.get(['adresseCommande'])!.value,
      etat: this.editForm.get(['etat'])!.value,
      dateCommande: this.editForm.get(['dateCommande'])!.value
        ? dayjs(this.editForm.get(['dateCommande'])!.value, DATE_TIME_FORMAT)
        : undefined,
      prixTotal: this.editForm.get(['prixTotal'])!.value,
      remisePerc: this.editForm.get(['remisePerc'])!.value,
      remiceVal: this.editForm.get(['remiceVal'])!.value,
      prixLivreson: this.editForm.get(['prixLivreson'])!.value,
      dateSortie: this.editForm.get(['dateSortie'])!.value ? dayjs(this.editForm.get(['dateSortie'])!.value, DATE_TIME_FORMAT) : undefined,
      livreur: this.editForm.get(['livreur'])!.value,
      client: this.editForm.get(['client'])!.value,
    };
  }
}
