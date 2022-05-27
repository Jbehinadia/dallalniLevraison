import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CommandeDetailsComponent } from './list/commande-details.component';
import { CommandeDetailsDetailComponent } from './detail/commande-details-detail.component';
import { CommandeDetailsUpdateComponent } from './update/commande-details-update.component';
import { CommandeDetailsDeleteDialogComponent } from './delete/commande-details-delete-dialog.component';
import { CommandeDetailsRoutingModule } from './route/commande-details-routing.module';

@NgModule({
  imports: [SharedModule, CommandeDetailsRoutingModule],
  declarations: [
    CommandeDetailsComponent,
    CommandeDetailsDetailComponent,
    CommandeDetailsUpdateComponent,
    CommandeDetailsDeleteDialogComponent,
  ],
  entryComponents: [CommandeDetailsDeleteDialogComponent],
})
export class CommandeDetailsModule {}
