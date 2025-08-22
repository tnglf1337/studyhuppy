export class Block {
  id?: string;
  modulId?: string;
  lernzeitSeconds: number;
  pausezeitSeconds: number;

  constructor(lernzeitSeconds: number, pausezeitSeconds: number, modulId? : string, id?: string) {
    this.id = id;
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
  id?: string;
  titel: string;
  beschreibung: string;
  blocks: Block[];

  constructor(titel: string, beschreibung: string, blocks: Block[] = [], id?: string) {
    this.id = id;
    this.titel = titel;
    this.beschreibung = beschreibung;
    this.blocks = blocks;
  }

  addBlock(block: Block): void {
    this.blocks.push(block);
  }

  getBlockAnzahl(): number {
    return this.blocks.length;
  }
}
