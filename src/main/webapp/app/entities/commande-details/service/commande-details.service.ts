import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICommandeDetails, getCommandeDetailsIdentifier } from '../commande-details.model';

export type EntityResponseType = HttpResponse<ICommandeDetails>;
export type EntityArrayResponseType = HttpResponse<ICommandeDetails[]>;

@Injectable({ providedIn: 'root' })
export class CommandeDetailsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/commande-details');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(commandeDetails: ICommandeDetails): Observable<EntityResponseType> {
    return this.http.post<ICommandeDetails>(this.resourceUrl, commandeDetails, { observe: 'response' });
  }

  update(commandeDetails: ICommandeDetails): Observable<EntityResponseType> {
    return this.http.put<ICommandeDetails>(
      `${this.resourceUrl}/${getCommandeDetailsIdentifier(commandeDetails) as number}`,
      commandeDetails,
      { observe: 'response' }
    );
  }

  partialUpdate(commandeDetails: ICommandeDetails): Observable<EntityResponseType> {
    return this.http.patch<ICommandeDetails>(
      `${this.resourceUrl}/${getCommandeDetailsIdentifier(commandeDetails) as number}`,
      commandeDetails,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICommandeDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICommandeDetails[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCommandeDetailsToCollectionIfMissing(
    commandeDetailsCollection: ICommandeDetails[],
    ...commandeDetailsToCheck: (ICommandeDetails | null | undefined)[]
  ): ICommandeDetails[] {
    const commandeDetails: ICommandeDetails[] = commandeDetailsToCheck.filter(isPresent);
    if (commandeDetails.length > 0) {
      const commandeDetailsCollectionIdentifiers = commandeDetailsCollection.map(
        commandeDetailsItem => getCommandeDetailsIdentifier(commandeDetailsItem)!
      );
      const commandeDetailsToAdd = commandeDetails.filter(commandeDetailsItem => {
        const commandeDetailsIdentifier = getCommandeDetailsIdentifier(commandeDetailsItem);
        if (commandeDetailsIdentifier == null || commandeDetailsCollectionIdentifiers.includes(commandeDetailsIdentifier)) {
          return false;
        }
        commandeDetailsCollectionIdentifiers.push(commandeDetailsIdentifier);
        return true;
      });
      return [...commandeDetailsToAdd, ...commandeDetailsCollection];
    }
    return commandeDetailsCollection;
  }
}
