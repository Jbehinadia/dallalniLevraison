import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MenuComponent } from './list/menu.component';
import { MenuDetailComponent } from './detail/menu-detail.component';
import { MenuUpdateComponent } from './update/menu-update.component';
import { MenuDeleteDialogComponent } from './delete/menu-delete-dialog.component';
import { MenuRoutingModule } from './route/menu-routing.module';

@NgModule({
  imports: [SharedModule, MenuRoutingModule],
  declarations: [MenuComponent, MenuDetailComponent, MenuUpdateComponent, MenuDeleteDialogComponent],
  entryComponents: [MenuDeleteDialogComponent],
})
export class MenuModule {}
