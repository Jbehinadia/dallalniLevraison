jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICommandeDetails, CommandeDetails } from '../commande-details.model';
import { CommandeDetailsService } from '../service/commande-details.service';

import { CommandeDetailsRoutingResolveService } from './commande-details-routing-resolve.service';

describe('CommandeDetails routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CommandeDetailsRoutingResolveService;
  let service: CommandeDetailsService;
  let resultCommandeDetails: ICommandeDetails | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CommandeDetailsRoutingResolveService);
    service = TestBed.inject(CommandeDetailsService);
    resultCommandeDetails = undefined;
  });

  describe('resolve', () => {
    it('should return ICommandeDetails returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCommandeDetails = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCommandeDetails).toEqual({ id: 123 });
    });

    it('should return new ICommandeDetails if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCommandeDetails = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCommandeDetails).toEqual(new CommandeDetails());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CommandeDetails })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCommandeDetails = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCommandeDetails).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
