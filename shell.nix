let
  nixpkgs = import <nixpkgs> {};

  ui = nixpkgs.stdenv.mkDerivation rec {
    name = "flax";
    version = "";
    src = ./.;
    buildInputs = [
      nixpkgs.sbt
      nixpkgs.scala
    ];
  };
in ui
