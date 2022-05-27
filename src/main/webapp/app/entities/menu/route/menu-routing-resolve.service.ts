import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMenu, Menu } from '../menu.model';
import { MenuService } from '../service/menu.service';

@Injectable({ providedIn: 'root' })
export class MenuRoutingResolveService implements Resolve<IMenu> {
  constructor(protected service: MenuService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMenu> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((menu: HttpResponse<Menu>) => {
          if (menu.body) {
            return of(menu.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Menu());
  }
}
