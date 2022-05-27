import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IResponsableRestaurant } from '../responsable-restaurant.model';
import { ResponsableRestaurantService } from '../service/responsable-restaurant.service';

@Component({
  templateUrl: './responsable-restaurant-delete-dialog.component.html',
})
export class ResponsableRestaurantDeleteDialogComponent {
  responsableRestaurant?: IResponsableRestaurant;

  constructor(protected responsableRestaurantService: ResponsableRestaurantService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.responsableRestaurantService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
