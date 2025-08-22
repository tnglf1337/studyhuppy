export interface Session {
  id:             string;
  titel:           string;
  beschreibung: number;
  blocks:   Block[];
}

export interface Block {
  id:               string;
  lernzeitSeconds:  number;
  pausezeitSeconds: number;
}
