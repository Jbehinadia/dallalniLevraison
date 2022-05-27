import { Pipe, PipeTransform } from '@angular/core';

import * as dayjs from 'dayjs';

@Pipe({
  name: 'duration',
})
export class DurationPipe implements PipeTransform {
  transform(value: any): string {
    if (value) {
      return dayjs().format();// dayjs.duration(value).humanize();
    }
    return '';
  }
}
