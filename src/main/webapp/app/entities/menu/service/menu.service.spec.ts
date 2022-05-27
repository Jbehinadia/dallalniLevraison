import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMenu, Menu } from '../menu.model';

import { MenuService } from './menu.service';

describe('Menu Service', () => {
  let service: MenuService;
  let httpMock: HttpTestingController;
  let elemDefault: IMenu;
  let expectedResult: IMenu | IMenu[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MenuService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomMenu: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Menu', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Menu()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Menu', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomMenu: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Menu', () => {
      const patchObject = Object.assign({}, new Menu());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Menu', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomMenu: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Menu', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMenuToCollectionIfMissing', () => {
      it('should add a Menu to an empty array', () => {
        const menu: IMenu = { id: 123 };
        expectedResult = service.addMenuToCollectionIfMissing([], menu);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menu);
      });

      it('should not add a Menu to an array that contains it', () => {
        const menu: IMenu = { id: 123 };
        const menuCollection: IMenu[] = [
          {
            ...menu,
          },
          { id: 456 },
        ];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, menu);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Menu to an array that doesn't contain it", () => {
        const menu: IMenu = { id: 123 };
        const menuCollection: IMenu[] = [{ id: 456 }];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, menu);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menu);
      });

      it('should add only unique Menu to an array', () => {
        const menuArray: IMenu[] = [{ id: 123 }, { id: 456 }, { id: 30359 }];
        const menuCollection: IMenu[] = [{ id: 123 }];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, ...menuArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const menu: IMenu = { id: 123 };
        const menu2: IMenu = { id: 456 };
        expectedResult = service.addMenuToCollectionIfMissing([], menu, menu2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menu);
        expect(expectedResult).toContain(menu2);
      });

      it('should accept null and undefined values', () => {
        const menu: IMenu = { id: 123 };
        expectedResult = service.addMenuToCollectionIfMissing([], null, menu, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menu);
      });

      it('should return initial array if no Menu is added', () => {
        const menuCollection: IMenu[] = [{ id: 123 }];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, undefined, null);
        expect(expectedResult).toEqual(menuCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
