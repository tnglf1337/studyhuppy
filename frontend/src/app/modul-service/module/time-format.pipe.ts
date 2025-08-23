import {Injectable, Pipe, PipeTransform} from '@angular/core';
@Injectable({
  providedIn: 'root'
})
@Pipe({
  standalone: true,
  name: 'timeFormat'
})
export class TimeFormatPipe implements PipeTransform {
  transform(secondsLearned: number | undefined): string {
    if (secondsLearned == null) {
      return '...';
    }

    let remaining = secondsLearned;

    const days = Math.floor(remaining / (24 * 3600));
    remaining %= (24 * 3600);

    const hours = Math.floor(remaining / 3600);
    remaining %= 3600;

    const minutes = Math.floor(remaining / 60);
    const seconds = remaining % 60;

    const parts: string[] = [];

    if (days > 0) parts.push(`${days}d`);
    if (hours > 0) parts.push(`${hours}h`);
    if (minutes > 0) parts.push(`${minutes}m`);
    if (seconds > 0) parts.push(`${seconds}s`);

    // Falls ALLE 0 → dann einfach "0s" anzeigen
    return parts.length > 0 ? parts.join(' ') : '0s';
  }
}
