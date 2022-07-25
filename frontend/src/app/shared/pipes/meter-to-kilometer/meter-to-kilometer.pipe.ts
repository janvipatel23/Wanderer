import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'meterToKilometer'
})
export class MeterToKilometerPipe implements PipeTransform {

  transform(meters: number): number {
    if(meters<0){
      meters = Math.abs(meters);
    }
    const meterToKilometerUnit: number = 1000.0;    
    return (meters/meterToKilometerUnit);
  }

}
