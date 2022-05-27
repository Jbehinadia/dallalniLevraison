import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MenuDetailComponent } from './menu-detail.component';

describe('Component Tests', () => {
  describe('Menu Management Detail Component', () => {
    let comp: MenuDetailComponent;
    let fixture: ComponentFixture<MenuDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [MenuDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ menu: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(MenuDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(MenuDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load menu on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.menu).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
