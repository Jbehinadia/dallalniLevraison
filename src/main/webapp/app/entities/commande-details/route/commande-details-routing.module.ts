import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CommandeDetailsComponent } from '../list/commande-details.component';
import { CommandeDetailsDetailComponent } from '../detail/commande-details-detail.component';
import { CommandeDetailsUpdateComponent } from '../update/commande-details-update.component';
import { CommandeDetailsRoutingResolveService } from './commande-details-routing-resolve.service';

const commandeDetailsRoute: Routes = [
  {
    path: '',
    component: CommandeDetailsComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CommandeDetailsDetailComponent,
    resolve: {
      commandeDetails: CommandeDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CommandeDetailsUpdateComponent,
    resolve: {
      commandeDetails: CommandeDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CommandeDetailsUpdateComponent,
    resolve: {
      commandeDetails: CommandeDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(commandeDetailsRoute)],
  exports: [RouterModule],
})
export class CommandeDetailsRoutingModule {}
