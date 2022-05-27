import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'client',
        data: { pageTitle: 'Clients' },
        loadChildren: () => import('./client/client.module').then(m => m.ClientModule),
      },
      {
        path: 'responsable-restaurant',
        data: { pageTitle: 'ResponsableRestaurants' },
        loadChildren: () => import('./responsable-restaurant/responsable-restaurant.module').then(m => m.ResponsableRestaurantModule),
      },
      {
        path: 'restaurant',
        data: { pageTitle: 'Restaurants' },
        loadChildren: () => import('./restaurant/restaurant.module').then(m => m.RestaurantModule),
      },
      {
        path: 'livreur',
        data: { pageTitle: 'Livreurs' },
        loadChildren: () => import('./livreur/livreur.module').then(m => m.LivreurModule),
      },
      {
        path: 'menu',
        data: { pageTitle: 'Menus' },
        loadChildren: () => import('./menu/menu.module').then(m => m.MenuModule),
      },
      {
        path: 'plat',
        data: { pageTitle: 'Plats' },
        loadChildren: () => import('./plat/plat.module').then(m => m.PlatModule),
      },
      {
        path: 'type-plat',
        data: { pageTitle: 'TypePlats' },
        loadChildren: () => import('./type-plat/type-plat.module').then(m => m.TypePlatModule),
      },
      {
        path: 'commande',
        data: { pageTitle: 'Commandes' },
        loadChildren: () => import('./commande/commande.module').then(m => m.CommandeModule),
      },
      {
        path: 'commande-details',
        data: { pageTitle: 'CommandeDetails' },
        loadChildren: () => import('./commande-details/commande-details.module').then(m => m.CommandeDetailsModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
