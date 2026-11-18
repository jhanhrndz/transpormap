type Point = { latitude: number; longitude: number };
type Category = 'poor_road' | 'full_closure' | 'partial_closure' | 'accident' | 'landslide_flood' | 'roadworks' | 'freight_restriction';
type Impact = 'open' | 'partial' | 'closed' | 'unknown';
type Severity = 'low' | 'medium' | 'high' | 'unknown';

export type AlertDraft = {
  title: string;
  description: string;
  category: Category;
  impact: Impact;
  severity: Severity;
  location: Point;
  observedAt: string;
};

export type PreparedAlert = Readonly<{ idempotencyKey: string; body: string }>;

function hasPlausibleTimestamp(value: string): boolean {
  const match = /^(\d{4})-(\d{2})-(\d{2})[Tt](\d{2}):(\d{2}):(\d{2})(?:\.\d+)?(?:[Zz]|([+-])(\d{2}):(\d{2}))$/.exec(value);
  if (!match) return false;

  const [, yearText, monthText, dayText, hourText, minuteText, secondText, , offsetHourText, offsetMinuteText] = match;
  const year = Number(yearText);
  const month = Number(monthText);
  const day = Number(dayText);
  const leapYear = year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0);
  const daysPerMonth = [31, leapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
  return month >= 1 && month <= 12 && day >= 1 && day <= daysPerMonth[month - 1]
    && Number(hourText) <= 23 && Number(minuteText) <= 59 && Number(secondText) <= 60
    && (offsetHourText === undefined || (Number(offsetHourText) <= 23 && Number(offsetMinuteText) <= 59));
}

export function prepareAlert(draft: AlertDraft, createId: () => string = () => crypto.randomUUID()): PreparedAlert {
  const categories: readonly string[] = ['poor_road', 'full_closure', 'partial_closure', 'accident', 'landslide_flood', 'roadworks', 'freight_restriction'];
  const impacts: readonly string[] = ['open', 'partial', 'closed', 'unknown'];
  const severities: readonly string[] = ['low', 'medium', 'high', 'unknown'];
  if (!categories.includes(draft.category) || !impacts.includes(draft.impact) || !severities.includes(draft.severity)) {
    throw new RangeError('La categoría, el impacto o la severidad no son válidos.');
  }
  if (typeof draft.observedAt !== 'string' || draft.observedAt.trim().length === 0) {
    throw new RangeError('La hora de observación es obligatoria.');
  }
  if (!hasPlausibleTimestamp(draft.observedAt)) {
    throw new RangeError('La fecha y hora de observación no tienen un formato válido.');
  }
  const titleLength = Array.from(draft.title).length;
  const descriptionLength = Array.from(draft.description).length;
  if (titleLength < 5 || titleLength > 100 || descriptionLength < 10 || descriptionLength > 500) {
    throw new RangeError('El título o la descripción están fuera de los límites permitidos.');
  }
  const { latitude, longitude } = draft.location;
  if (!Number.isFinite(latitude) || latitude < -90 || latitude > 90 ||
      !Number.isFinite(longitude) || longitude < -180 || longitude > 180) {
    throw new RangeError('La ubicación no es válida.');
  }

  const clientReportId = createId();
  const idempotencyKey = createId();
  return Object.freeze({
    idempotencyKey,
    body: JSON.stringify({
      clientReportId,
      title: draft.title,
      description: draft.description,
      category: draft.category,
      impact: draft.impact,
      severity: draft.severity,
      location: { latitude, longitude },
      observedAt: draft.observedAt,
      photoIntent: 'none',
    }),
  });
}
