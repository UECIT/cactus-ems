import * as moment from 'moment';
import { Moment } from 'moment';

export class Age {
  constructor(dateOfBirthStr: string, dateEndStr?: string) {
    const dateEndTemp = moment(dateEndStr);

    this.dateOfBirth = moment(dateOfBirthStr);
    this.dateEnd =
      dateEndStr !== undefined && dateEndTemp.isValid()
        ? dateEndTemp
        : moment();

    if (
      dateOfBirthStr === undefined ||
      !this.dateOfBirth.isValid() ||
      !this.dateEnd.isValid()
    ) {
      throw new Error('Invalid dates.' + dateOfBirthStr + ' and ' + dateEndStr);
    }

    this.days = this.dateEnd.date() - this.dateOfBirth.date();

    if (this.days < 0) {
      this.days += this.dateOfBirth.daysInMonth();
      this.months -= 1;
    }

    this.months += this.dateEnd.month() - this.dateOfBirth.month();

    if (this.months < 0) {
      this.months += 12;
      this.years -= 1;
    }

    this.years += this.dateEnd.year() - this.dateOfBirth.year();
  }

  public years = 0;

  public months = 0;

  public days = 0;

  get weeks(): number {
    return this.days / 7;
  }

  get totalHours(): number {
    return this.dateEnd.diff(this.dateOfBirth, 'hours', true);
  }

  get totalDays(): number {
    return this.dateEnd.diff(this.dateOfBirth, 'days');
  }

  get totalWeeks(): number {
    return this.dateEnd.diff(this.dateOfBirth, 'weeks');
  }

  get totalMonths(): number {
    return this.dateEnd.diff(this.dateOfBirth, 'months');
  }

  private dateOfBirth: Moment;

  private dateEnd: Moment;

  toString(): string {
    if (this.totalHours < 2.0) {
      return `${Math.floor(this.totalHours * 60)}min`;
    } else if (this.totalDays < 2.0) {
      return `${Math.floor(this.totalHours)}hrs`;
    } else if (this.totalDays / 7 < 4.0) {
      return `${this.totalDays}d`;
    } else if (this.years < 1.0) {
      return this.totalDays % 7 < 1
        ? `${this.totalWeeks}w`
        : `${Math.floor(this.totalDays / 7)}w ${this.totalDays % 7}d`;
    } else if (this.years < 2.0) {
      return this.days === 0
        ? `${this.totalMonths}m`
        : `${this.totalMonths}m ${this.days}d`;
    } else if (this.years < 18.0) {
      return this.months === 0
        ? `${this.years}y`
        : `${this.years}y ${this.months}m`;
    }

    return `${this.years}y`;
  }
}
