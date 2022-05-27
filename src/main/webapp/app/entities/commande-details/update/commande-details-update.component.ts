import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICommandeDetails, CommandeDetails } from '../commande-details.model';
import { CommandeDetailsService } from '../service/commande-details.service';
import { ICommande } from 'app/entities/commande/commande.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { IPlat } from 'app/entities/plat/plat.model';
import { PlatService } from 'app/entities/plat/service/plat.service';

@Component({
  selector: 'jhi-commande-details-update',
  templateUrl: './commande-details-update.component.html',
})
export class CommandeDetailsUpdateComponent implements OnInit {
  isSaving = false;

  commandesSharedCollection: ICommande[] = [];
  platsSharedCollection: IPlat[] = [];

  editForm = this.fb.group({
    id: [],
    prix: [],
    etat: [],
    commande: [],
    plat: [],
  });

  constructor(
    protected commandeDetailsService: CommandeDetailsService,
    protected commandeService: CommandeService,
    protected platService: PlatService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commandeDetails }) => {
      this.updateForm(commandeDetails);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const commandeDetails = this.createFromForm();
    if (commandeDetails.id !== undefined) {
      this.subscribeToSaveResponse(this.commandeDetailsService.update(commandeDetails));
    } else {
      this.subscribeToSaveResponse(this.commandeDetailsService.create(commandeDetails));
    }
  }

  trackCommandeById(_index: number, item: ICommande): number {
    return item.id!;
  }

  trackPlatById(_index: number, item: IPlat): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICommandeDetails>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
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

  protected updateForm(commandeDetails: ICommandeDetails): void {
    this.editForm.patchValue({
      id: commandeDetails.id,
      prix: commandeDetails.prix,
      etat: commandeDetails.etat,
      commande: commandeDetails.commande,
      plat: commandeDetails.plat,
    });

    this.commandesSharedCollection = this.commandeService.addCommandeToCollectionIfMissing(
      this.commandesSharedCollection,
      commandeDetails.commande
    );
    this.platsSharedCollection = this.platService.addPlatToCollectionIfMissing(this.platsSharedCollection, commandeDetails.plat);
  }

  protected loadRelationshipsOptions(): void {
    this.commandeService
      .query()
      .pipe(map((res: HttpResponse<ICommande[]>) => res.body ?? []))
      .pipe(
        map((commandes: ICommande[]) =>
          this.commandeService.addCommandeToCollectionIfMissing(commandes, this.editForm.get('commande')!.value)
        )
      )
      .subscribe((commandes: ICommande[]) => (this.commandesSharedCollection = commandes));

    this.platService
      .query()
      .pipe(map((res: HttpResponse<IPlat[]>) => res.body ?? []))
      .pipe(map((plats: IPlat[]) => this.platService.addPlatToCollectionIfMissing(plats, this.editForm.get('plat')!.value)))
      .subscribe((plats: IPlat[]) => (this.platsSharedCollection = plats));
  }

  protected createFromForm(): ICommandeDetails {
    return {
      ...new CommandeDetails(),
      id: this.editForm.get(['id'])!.value,
      prix: this.editForm.get(['prix'])!.value,
      etat: this.editForm.get(['etat'])!.value,
      commande: this.editForm.get(['commande'])!.value,
      plat: this.editForm.get(['plat'])!.value,
    };
  }
}
