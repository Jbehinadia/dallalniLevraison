import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResponsableRestaurant, ResponsableRestaurant } from '../responsable-restaurant.model';
import { ResponsableRestaurantService } from '../service/responsable-restaurant.service';

@Injectable({ providedIn: 'root' })
export class ResponsableRestaurantRoutingResolveService implements Resolve<IResponsableRestaurant> {
  constructor(protected service: ResponsableRestaurantService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResponsableRestaurant> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((responsableRestaurant: HttpResponse<ResponsableRestaurant>) => {
          if (responsableRestaurant.body) {
            return of(responsableRestaurant.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ResponsableRestaurant());
  }
}
