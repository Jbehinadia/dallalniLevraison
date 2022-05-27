jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ILivreur, Livreur } from '../livreur.model';
import { LivreurService } from '../service/livreur.service';

import { LivreurRoutingResolveService } from './livreur-routing-resolve.service';

describe('Livreur routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: LivreurRoutingResolveService;
  let service: LivreurService;
  let resultLivreur: ILivreur | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(LivreurRoutingResolveService);
    service = TestBed.inject(LivreurService);
    resultLivreur = undefined;
  });

  describe('resolve', () => {
    it('should return ILivreur returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLivreur = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLivreur).toEqual({ id: 123 });
    });

    it('should return new ILivreur if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLivreur = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultLivreur).toEqual(new Livreur());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Livreur })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLivreur = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLivreur).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
