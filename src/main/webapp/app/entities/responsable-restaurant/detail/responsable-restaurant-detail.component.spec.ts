import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ResponsableRestaurantDetailComponent } from './responsable-restaurant-detail.component';

describe('ResponsableRestaurant Management Detail Component', () => {
  let comp: ResponsableRestaurantDetailComponent;
  let fixture: ComponentFixture<ResponsableRestaurantDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResponsableRestaurantDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ responsableRestaurant: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ResponsableRestaurantDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ResponsableRestaurantDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load responsableRestaurant on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.responsableRestaurant).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
