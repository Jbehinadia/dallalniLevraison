import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ResponsableRestaurantComponent } from './list/responsable-restaurant.component';
import { ResponsableRestaurantDetailComponent } from './detail/responsable-restaurant-detail.component';
import { ResponsableRestaurantUpdateComponent } from './update/responsable-restaurant-update.component';
import { ResponsableRestaurantDeleteDialogComponent } from './delete/responsable-restaurant-delete-dialog.component';
import { ResponsableRestaurantRoutingModule } from './route/responsable-restaurant-routing.module';

@NgModule({
  imports: [SharedModule, ResponsableRestaurantRoutingModule],
  declarations: [
    ResponsableRestaurantComponent,
    ResponsableRestaurantDetailComponent,
    ResponsableRestaurantUpdateComponent,
    ResponsableRestaurantDeleteDialogComponent,
  ],
  entryComponents: [ResponsableRestaurantDeleteDialogComponent],
})
export class ResponsableRestaurantModule {}
