export type ScheduleType = 'REGULAR' | 'EXPRESS' | 'NIGHT' | 'WEEKEND' | 'HOLIDAY' | 'SPECIAL';

export const SCHEDULE_TYPES: ScheduleType[] = ['REGULAR', 'EXPRESS', 'NIGHT', 'WEEKEND', 'HOLIDAY', 'SPECIAL'];

export const getScheduleTypeLabel = (type: ScheduleType): string => {
  switch (type) {
    case 'REGULAR': return 'Régulier';
    case 'EXPRESS': return 'Express';
    case 'NIGHT': return 'Nocturne';
    case 'WEEKEND': return 'Week-end';
    case 'HOLIDAY': return 'Vacances';
    case 'SPECIAL': return 'Spécial';
    default: return type;
  }
};

export const getScheduleTypeColor = (type: ScheduleType): string => {
  switch (type) {
    case 'REGULAR': return 'bg-blue-100 text-blue-700 border-blue-200';
    case 'EXPRESS': return 'bg-purple-100 text-purple-700 border-purple-200';
    case 'NIGHT': return 'bg-gray-800 text-white border-gray-900';
    case 'WEEKEND': return 'bg-green-100 text-green-700 border-green-200';
    case 'HOLIDAY': return 'bg-orange-100 text-orange-700 border-orange-200';
    case 'SPECIAL': return 'bg-pink-100 text-pink-700 border-pink-200';
    default: return 'bg-gray-100 text-gray-700 border-gray-200';
  }
};

