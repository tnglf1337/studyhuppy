export interface Session {
  id:             string;
  titel:           string;
  beschreibung: string;
  blocks:   Block[];
}

export interface Block {
  id:               string;
  lernzeitSeconds:  number;
  pausezeitSeconds: number;
}
