import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { FutureTripService } from './future-trip.service';

describe('FutureTripService', () => {
  let service: FutureTripService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(FutureTripService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
