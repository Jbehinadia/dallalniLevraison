jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMenu, Menu } from '../menu.model';
import { MenuService } from '../service/menu.service';

import { MenuRoutingResolveService } from './menu-routing-resolve.service';

describe('Menu routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: MenuRoutingResolveService;
  let service: MenuService;
  let resultMenu: IMenu | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(MenuRoutingResolveService);
    service = TestBed.inject(MenuService);
    resultMenu = undefined;
  });

  describe('resolve', () => {
    it('should return IMenu returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMenu = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMenu).toEqual({ id: 123 });
    });

    it('should return new IMenu if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMenu = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultMenu).toEqual(new Menu());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Menu })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMenu = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMenu).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
