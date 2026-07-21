import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const passwordStrength: ValidatorFn = (c: AbstractControl): ValidationErrors | null => {
  const v = (c.value ?? '') as string;
  if (!v) return null;
  const ok = v.length >= 8 && /[A-Za-z]/.test(v) && /\d/.test(v);
  return ok ? null : { weakPassword: true };
};
