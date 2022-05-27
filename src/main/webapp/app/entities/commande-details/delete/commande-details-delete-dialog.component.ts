import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommandeDetails } from '../commande-details.model';
import { CommandeDetailsService } from '../service/commande-details.service';

@Component({
  templateUrl: './commande-details-delete-dialog.component.html',
})
export class CommandeDetailsDeleteDialogComponent {
  commandeDetails?: ICommandeDetails;

  constructor(protected commandeDetailsService: CommandeDetailsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.commandeDetailsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
