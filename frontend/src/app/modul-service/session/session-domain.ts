export class Block {
  fachid?: string;
  modulId?: string;
  lernzeitSeconds: number;
  pausezeitSeconds: number;

  constructor(lernzeitSeconds: number, pausezeitSeconds: number, fachId?: string, modulId? : string) {
    this.fachid = fachId;
    this.modulId = modulId;
    this.lernzeitSeconds = lernzeitSeconds;
    this.pausezeitSeconds = pausezeitSeconds;
  }

  setModulId(modulId: string): void {
    this.modulId = modulId;
  }

  setLernzeitSeconds(seconds: number): void {
    this.lernzeitSeconds = seconds;
  }

  setPausezeitSeconds(seconds: number): void {
    this.pausezeitSeconds = seconds;
  }
}

export class Session {
  fachId?: string;
  titel: string;
  beschreibung: string;
  blocks: Block[];

  constructor(titel: string, beschreibung: string, blocks: Block[] = [], fachId?: string) {
    this.fachId = fachId;
    this.titel = titel;
    this.beschreibung = beschreibung;
    this.blocks = blocks;
  }

  validSession(): boolean {
    let isValid = true;

    for (const block of this.blocks) {
      if (!block.modulId || block.lernzeitSeconds <= 0 || block.pausezeitSeconds < 0) {
        isValid = false;
        break;
      }
    }

    return isValid
  }
}
