export class Case {
  id: number;
  skillset: Code;
  party: Code;
}

export class Code {
  id: number;
  code: string;
  display: string;
  description: string;
}
