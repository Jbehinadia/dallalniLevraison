import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICommandeDetails, CommandeDetails } from '../commande-details.model';
import { CommandeDetailsService } from '../service/commande-details.service';

@Injectable({ providedIn: 'root' })
export class CommandeDetailsRoutingResolveService implements Resolve<ICommandeDetails> {
  constructor(protected service: CommandeDetailsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICommandeDetails> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((commandeDetails: HttpResponse<CommandeDetails>) => {
          if (commandeDetails.body) {
            return of(commandeDetails.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CommandeDetails());
  }
}
