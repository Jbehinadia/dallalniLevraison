import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { CommandePourClientComponent } from './list-pour-client/commande-pour-client.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent, CommandePourClientComponent],
  entryComponents: [CommandePourClientComponent],
})
export class HomeModule {}
